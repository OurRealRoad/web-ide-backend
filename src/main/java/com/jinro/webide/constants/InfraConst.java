package com.jinro.webide.constants;

public final class InfraConst {
    public static final String DOCKER_SERVER_IP = "localhost";
    public static final String DOCKER_DAEMON = "tcp://" + DOCKER_SERVER_IP + ":2375";

    // for ssh port
    public static final int EXPOSED_PORT = 22;

    // docker image
    public static final String IMAGE_ID = "7286bf2303b506b08ea23742ad56b223a5c118cee8689018a09bda398dd9a668";

    // cpu 제한 : 0.5v
    public static final Long LIMIT_CPU = 500000000L;

    // 메모리 제한 512MB
    public static final Long LIMIT_MEM = 536870912L;

    // 스왑 메모리 제한: 1GB
    public static final Long LIMIT_SWAP = 1073741824L;

    // rsa key path
    public static final String RSA_PATH = "/key/id_rsa";

    // ssh account
    public static final String SSH_ACCOUNT = "root";

    private InfraConst() {

    }
}