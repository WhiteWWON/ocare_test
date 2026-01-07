package com.ocare.Ocare.adapter.out.persistence.entity;

import com.ocare.Ocare.domain.model.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "OC_MEMBER")
@Getter
@NoArgsConstructor
public class MemberJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long member_id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String name;
    private String nickname;

    @CreationTimestamp
    private LocalDateTime last_login_dt;

    @Column(name = "login_fail_cnt")
    private Integer login_fail_cnt = 0;

    @CreationTimestamp
    private LocalDateTime created_dt;
    private String created_id;

    @CreationTimestamp
    @Column(name = "updated_dt")
    private LocalDateTime updated_dt;
    private String updated_id;

    // 도메인 모델 -> JPA 엔티티 변환 (저장할 때 사용)
    public static MemberJpaEntity fromDomain(Member member) {
        MemberJpaEntity entity = new MemberJpaEntity();
        entity.email = member.getEmail();
        entity.password = member.getPassword();
        entity.name = member.getName();
        entity.nickname = member.getNickname();
        entity.login_fail_cnt = member.getLoginFailCnt();
        entity.created_id = member.getCreatedId();
        return entity;
    }

    // JPA 엔티티 -> 도메인 모델 변환 (조회할 때 사용)
    public Member toDomain() {
        return Member.builder()
                .memberId(this.member_id)
                .email(this.email)
                .password(this.password)
                .name(this.name)
                .nickname(this.nickname)
                .loginFailCnt(this.login_fail_cnt)
                .createdId(this.created_id)
                .build();
    }

}
