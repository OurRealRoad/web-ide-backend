package com.jinro.webide.constants;

public final class InfraConst {
    public static final String HOST = "localhost";
    public static final String DOCKER_DAEMON = "tcp://" + HOST + ":2375";

    // for ssh port
    public static final int EXPOSED_PORT = 22;

    // docker image
    public static final String IMAGE_NAME = "d8b7255925683e834cbe9d616c5d5656332619e8839eb9f21a1f9a20266057de";

    // cpu 제한 : 0.5v
    public static final Long LIMIT_CPU = 500000000L;

    // 메모리 제한 512MB
    public static final Long LIMIT_MEM = 536870912L;

    // 스왑 메모리 제한: 1GB
    public static final Long LIMIT_SWAP = 1073741824L;

    // rsa key path
    public static final String RSA_PATH = "/key/id_rsa";

    // ssh account
    public static final String SSH_USER = "root";

    private InfraConst() {
    }
}