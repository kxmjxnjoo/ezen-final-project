package com.ezen.springfeed.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ezen.springfeed.dto.MemberDto;
import com.ezen.springfeed.service.MemberService;

@Controller
public class MemberController {

	@Autowired
	MemberService ms;
	
	//로그인 폼으로
	@RequestMapping(value="/login/form")
	public String loginForm() {
		return "member/login";
	}
    
    //로그인 액션
    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String login(@ModelAttribute("dto") @Valid MemberDto memberdto,
    		BindingResult result, HttpServletRequest request,
    		Model model) {
    	
    	String url = "member/login";
    	
    	System.out.println(memberdto.getUserid());
    	if(memberdto.getUserid() == null || memberdto.getUserid().equals("")) {
    		model.addAttribute("message", "아이디를 입력해주세요");
    	} else if(memberdto.getUserpwd() == null || memberdto.getUserpwd().equals("")) {
    		model.addAttribute("message", "비밀번호를 입력해주세요");
    	} else {
    		HashMap<String, Object> paramMap = new HashMap<>();
    		paramMap.put("userid", memberdto.getUserid());
    		paramMap.put("ref_cursor", null);
    		ms.getMember(paramMap);
    		
    		ArrayList<HashMap<String, Object>> list
    			= (ArrayList<HashMap<String, Object>>) paramMap.get("ref_cursor");
    		if(list.size() == 0) {
    			model.addAttribute("message", "잘못된 사용자 아이디입니다. 다시 확인하세요.");
    			return "member/login";
    		} 
    		
    		HashMap<String, Object> mvo = list.get(0);
    		if(mvo.get("PASSWORD") == null) {
    			model.addAttribute("message", "로그인에 문제가 발생했어요:( QnA를 남겨주시면 빠르게 해결해드릴게요!");
    			//고객센터로 연결하는 버튼 모달 만들기

    		} else if (memberdto.getUserpwd().equals((String)mvo.get("PASSWORD"))) {
    			HttpSession session = request.getSession();
    			session.setAttribute("loginUser", mvo);
    			url = "redirect:/";
    		} else {
    			model.addAttribute("message", "잘못된 비밀번호입니다. 다시 확인하세요.");
    		}
    	}
        return url;
    }

    //로그아웃
    @RequestMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute("loginUser");
        return "redirect:/";
    }

    
    //이용약관
    
    

    // 회원가입 폼
    @RequestMapping("/join/form")
    public String joinForm() {
        return "member/join";
    }
    
    
    //회원가입 시 중복, 유효성 체크 
    @RequestMapping(value="/join/idCheck", method=RequestMethod.POST)
    @ResponseBody
    public int idCheck(@ModelAttribute("dto") @Valid MemberDto memberdto,
    		BindingResult result) {

    	int cnt = 1;
    	
    	if(memberdto.getUserid() != null) {
        	System.out.println("userIdCheck 진입");
        	System.out.println("전달 받은 id = " + memberdto.getUserid());
        	String userid = memberdto.getUserid();
	    	HashMap<String, Object> paramMap = new HashMap<>();
			paramMap.put("cnt", 0);
			paramMap.put("userid", userid);
			
	    	ms.idCheck(paramMap);
	    	
	    	
	    	if(result.getFieldError("userid") == null) 
	    		cnt = Integer.parseInt(paramMap.get("cnt").toString());
	    	
	    	System.out.println("확인 결과 : " + cnt );
    	} else if (memberdto.getEmail() != null) {
    		if(result.getFieldError("email") == null) cnt = 0;
    	} else if (memberdto.getPhone() != null) {
    		if(result.getFieldError("phone") == null) cnt = 0;
    	} else if (memberdto.getName() != null) {
    		if(result.getFieldError("name") == null) cnt = 0;
    	} else if (memberdto.getUserpwd() != null) {
    		if(result.getFieldError("userpwd") == null) cnt = 0;
    	}
    	
    	return cnt;
    }
    
    
    // 회원가입 액션
    @RequestMapping(value="/join", method=RequestMethod.POST)
    public String join(@ModelAttribute("dto") @Valid MemberDto memberdto,
    		BindingResult result,
    		@RequestParam(value="reid", required=false) String reid,
    		HttpServletRequest request, Model model) {
    	
    	model.addAttribute("reid", reid);
    	String url = "member/join";
    	
    	if(result.getFieldError("phone")!= null) {
            model.addAttribute("message", result.getFieldError("phone").getDefaultMessage());
         } else if(result.getFieldError("email")!= null) {
            model.addAttribute("message", result.getFieldError("email").getDefaultMessage());
         } else if(result.getFieldError("name")!= null) {
            model.addAttribute("message", result.getFieldError("name").getDefaultMessage());
         } else if(result.getFieldError("userid")!= null) {
            model.addAttribute("message", result.getFieldError("userid").getDefaultMessage());
         } else if(result.getFieldError("userpwd")!= null) {
            model.addAttribute("message", result.getFieldError("userpwd").getDefaultMessage());
         } else {
 			
 			HashMap<String, Object> paramMap = new HashMap<>();
 			paramMap.put("userid",memberdto.getUserid());
 			paramMap.put("userpwd",memberdto.getUserpwd());
 			paramMap.put("name",memberdto.getName());
 			paramMap.put("email",memberdto.getEmail());
 			paramMap.put("phone",memberdto.getPhone());
 			
 			ms.insertMember(paramMap);
 			
 			model.addAttribute("message", "회원가입이 완료되었어요:) 로그인 후 이용해주세요.");
 			url = "member/login";
 		}
    	
        return url;
    }

///////////////////////////////////////////////
    //팔로우도 ajax로 처리해야할 듯 함
    
    // 팔로우 
    @RequestMapping("/follow")
    public String follow(HttpServletRequest request, 
    		@RequestParam("userid") String userid,
    		Model model) {
    	HttpSession session = request.getSession();
		
    	String url = "";
		HashMap<String, Object> loginUser = 
				(HashMap<String, Object>) session.getAttribute("loginUser");
		
		if (loginUser == null) { 
			url = "member/login";
		} else {
			
			HashMap<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("follower", (String) loginUser.get("userid"));
			paramMap.put("followed", userid);
			paramMap.put("result", 0);
			
			ms.insertFollow(paramMap);
			
			int result = Integer.parseInt(paramMap.get("result").toString());
			
			if(result == 1) {
				model.addAttribute("message", userid+"님을 팔로우 했어요");
				
				paramMap.put("notitype", 1);
				paramMap.put("notiresult", 0);
				
				ms.addNotification(paramMap);
				
			} else {
				model.addAttribute("message", "오류가 발생했어요:( 다시 시도해주세요.");
			}
			url = "redirect:/";
		}
    	
        return url;
    }

    // 언팔로우
    @RequestMapping("/unfollow")
    public String unfollow(HttpServletRequest request, 
    		@RequestParam("userid") String userid,
    		Model model) {
    	
    	HttpSession session = request.getSession();
		
    	String url = "";
		HashMap<String, Object> loginUser = 
				(HashMap<String, Object>) session.getAttribute("loginUser");
		
		if (loginUser == null) { 
			url = "member/login";
		} else {
			
			String follower = (String)loginUser.get("userid");
			if(!follower.equals(userid)) {
				HashMap<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("follower", follower);
				paramMap.put("followed", userid);
				paramMap.put("result", 0);
				
				ms.unfollow(paramMap);
			}
			
		}
        return "";
    }
    
    @RequestMapping("/deleteAcount") 
    public String deleteAcount(HttpServletRequest request,
    		@RequestParam("userid") String userid) {
    	HttpSession session = request.getSession();
		
    	String url = "";
		HashMap<String, Object> loginUser = 
				(HashMap<String, Object>) session.getAttribute("loginUser");
		
		if (loginUser == null) { 
			url = "member/login";
		} else {
			
			HashMap<String, Object> paramMap = new HashMap<String, Object>();
			
			
			url = "";
		}
		
		return url;
    }
    
    @RequestMapping("/noti")
    public ModelAndView Notification(HttpServletRequest request, Model model) {
    	HttpSession session = request.getSession();
		
    	ModelAndView mav = new ModelAndView();
    	String url = "";
    	
		HashMap<String, Object> loginUser = 
				(HashMap<String, Object>) session.getAttribute("loginUser");
		
		if (loginUser == null) { 
			url = "redirect:/login/form";
		} else {
			
			HashMap<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("ref_cursor", null);
			paramMap.put("userid", loginUser.get("USERID"));
			ms.getNotification(paramMap);
			
			ArrayList<HashMap<String, Object>> notiList 
				= (ArrayList<HashMap<String, Object>>) paramMap.get("ref_cursor");
			
			String postImg = "";
			String replyContent = "";
			if(notiList != null) {
				for(HashMap<String, Object> temp : notiList) {
//					LocalDate now = LocalDate.now();
//					LocalDate notiDate = ((timeStamp) temp.get("CREATE_DATE")).toLocalDate();
//					Period period = Period.between(now, notiDate);
//					long diff = Math.abs(period.getDays());
//					
//					if(diff == 0) {
//						temp.replace("CREATE_DATE", "오늘");
//					} else if(diff < 30) {
//						temp.replace("CREATE_DATE", diff+"일 전");
//					} else if(diff < 365) {
//						temp.replace("CREATE_DATE", Math.abs(diff / 30) + "달 전");
//					} else {
//						temp.replace("CREATE_DATE", "오래 전");
//					}
						
				}
			}	

			mav.addObject("notiList", notiList);
			
			mav.setViewName("noti/noti");	
		}
		return mav;
    }
    
}
