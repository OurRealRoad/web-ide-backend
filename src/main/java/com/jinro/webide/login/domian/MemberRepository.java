package com.jinro.webide.login.domian;

import java.util.Optional;

public interface MemberRepository {

    Member save(Member member);

    Optional<Member> findByEmail(String email);

    Optional<Member> findMemberByName(String name);

}

