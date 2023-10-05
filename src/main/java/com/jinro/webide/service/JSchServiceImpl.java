package com.jinro.webide.service;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jinro.webide.constants.InfraConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class JSchServiceImpl implements JSchService {

    private final Map<String, JSchProjectConnection> projectSession = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public void createSession(String projectId, int port) throws JSchException, IOException {
        JSch jsch = new JSch();
        // ToDo : resource에서 얻어오는게 아닌 보안을 지키게 다른곳에서 얻어오게 나중에 변경
        ClassPathResource resource = new ClassPathResource(InfraConst.RSA_PATH);
        File file = resource.getFile();
        jsch.addIdentity(file.getAbsolutePath());

        Session session = jsch.getSession(InfraConst.SSH_USER, InfraConst.HOST, port);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        ChannelShell channelShell = createChannelShell(session);
        projectSession.put(projectId, new JSchProjectConnection(session, channelShell));
    }

    public ChannelShell createChannelShell(Session session) throws JSchException {
        ChannelShell channelShell = (ChannelShell) session.openChannel("shell");
        channelShell.setPty(true);
        channelShell.setPtyType("xterm");
        channelShell.connect();
        return channelShell;
    }

    public void execute(String projectId, String command, WebSocketSession session) throws JSchException, IOException {
        ChannelShell shell = projectSession.get(projectId).getChannelShell();
        InputStream in = shell.getInputStream();
        OutputStream os = shell.getOutputStream();

        if (command.equals("SIGINT")) {
            os.write(3);
        } else if (command.equals("SIGTSTP")) {
            os.write(26);
        } else {
            os.write(command.getBytes());
        }
        os.flush();

        executorService.execute(() -> {
            byte[] buffer = new byte[1024];
            int i = 0;
            try {
                while ((i = in.read(buffer)) != -1) {
                    session.sendMessage(new TextMessage(new String(buffer, 0, i)));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void disconnect(String projectId) throws JSchException, IOException {
        projectSession.get(projectId).disconnect();
    }
}