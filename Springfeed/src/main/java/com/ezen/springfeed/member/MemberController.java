package com.ezen.springfeed.member;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/member")
public class MemberController {

    private final MemberService ms;

    public MemberController(MemberService ms) {
        this.ms = ms;
    }

    @GetMapping("/{userid}")
    public Member getMember(@PathVariable String userid) {
        return ms.getMember(userid);
    }

    // Get userid by name and email
    @RequestMapping("/userid")
    @GetMapping
    public Member getUseridByNameAndEmail(@RequestBody Member member) {
        return ms.getMemberByNameAndEmail(member);
    }

    // login
    @RequestMapping("/login")
    @PostMapping
    public void login(@RequestBody Member member) {

    }

    @PostMapping
    public void addMember(@RequestBody Member member) {
        ms.addMember(member);
    }

    @PutMapping
    public void updateMember(@RequestBody Member member) {
        ms.updateMember(member);
    }

    @DeleteMapping
    public void deleteMember(@RequestBody Member member) {
        ms.deleteMember(member);
    }
}
