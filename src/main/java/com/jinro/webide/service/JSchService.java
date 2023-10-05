package com.jinro.webide.service;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public interface JSchService {

    void createSession(String projectId, int port) throws JSchException, IOException;

    ChannelShell createChannelShell(Session session) throws JSchException;

    void execute(String projecgId, String command, WebSocketSession session) throws JSchException, IOException;

    void disconnect(String projectId) throws JSchException, IOException;
}