package com.jinro.webide.constants;

public final class InfraConst {
    public static final String DOCKER_SERVER_IP = System.getenv("Docker_Server_IP");
    public static final String DOCKER_DAEMON = "tcp://" + DOCKER_SERVER_IP + ":" + System.getenv("Docker_Daemon_port");

    // for ssh port
    public static final int EXPOSED_PORT = 22;

    // docker image
    public static final String IMAGE_ID = System.getenv("Docker_image_id");

    // cpu 제한 : 0.5v
    public static final Long LIMIT_CPU = 500000000L;

    // 메모리 제한 512MB
    public static final Long LIMIT_MEM = 536870912L;

    // 스왑 메모리 제한: 1GB
    public static final Long LIMIT_SWAP = 1073741824L;

    // rsa key path
    public static final String RSA_PATH = System.getenv("RSA_Path");

    // ssh account
    public static final String SSH_ACCOUNT = System.getenv("SSH_account");

    private InfraConst() {
    }
}