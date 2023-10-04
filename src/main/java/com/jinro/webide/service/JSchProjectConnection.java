package com.jinro.webide.service;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.IOException;

public class JSchProjectConnection {
    private final Session session;
    private final ChannelShell channelShell;

    public JSchProjectConnection(Session session, ChannelShell channelShell) {
        this.session = session;
        this.channelShell = channelShell;
    }

    public Session getSession() throws JSchException {
        validateSession();
        return session;
    }

    public ChannelShell getChannelShell() throws JSchException {
        validateChannelShell();
        return channelShell;
    }

    public void disconnect() throws JSchException, IOException {
        validateSession();
        validateChannelShell();
        this.channelShell.getInputStream().close();
        this.channelShell.getOutputStream().close();
        this.channelShell.disconnect();
        this.session.disconnect();
    }

    public void validateSession() throws JSchException {
        if (session == null || !session.isConnected()) {
            throw new JSchException("Session is not connected");
        }
    }

    public void validateChannelShell() throws JSchException {
        if (channelShell == null || !channelShell.isConnected()) {
            throw new JSchException("ChannelShell is not connected");
        }
    }
}