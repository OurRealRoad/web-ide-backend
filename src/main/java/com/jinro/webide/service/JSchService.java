package com.jinro.webide.service;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.UUID;

public interface JSchService {

    void createSession(UUID projectId, int port) throws JSchException, IOException;

    ChannelShell createChannelShell(Session session) throws JSchException;

    void execute(UUID projecgId, String command, WebSocketSession session) throws JSchException, IOException;

    void disconnect(UUID projectId) throws JSchException, IOException;
}