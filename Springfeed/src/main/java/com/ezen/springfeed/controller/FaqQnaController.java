package com.ezen.springfeed.controller;

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
import org.springframework.web.servlet.ModelAndView;

import com.ezen.springfeed.dto.FaqDto;
import com.ezen.springfeed.dto.Paging;
import com.ezen.springfeed.dto.QnaDto;
import com.ezen.springfeed.service.AdminService;
import com.ezen.springfeed.service.FaqQnaService;

@Controller
public class FaqQnaController {

	@Autowired
	FaqQnaService fqs;
	
	@Autowired
	AdminService as;
	
	@RequestMapping("/admin/faqList")
	public ModelAndView adminFaqList(Model model, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		
		HttpSession session = request.getSession();
		HashMap<String,Object> loginAdmin
			= (HashMap<String, Object>) session.getAttribute("loginAdmin");
		System.out.println(5);
	    if (loginAdmin == null) {
	    	mav.setViewName("admin/admingLogin");
	    	return mav;
	    } else {
	    	HashMap<String,Object> paramMap = new HashMap<>();
	    	paramMap.put("ref_cursor", null);
	    	fqs.adminFaqList(paramMap);
	    System.out.println(6);	
	    	ArrayList<HashMap<String,Object>> list
	    		= (ArrayList<HashMap<String, Object>>) paramMap.get("ref_cursor");
	    	mav.addObject("adminFaqList", list);
	    	mav.setViewName("admin/faq/adminFaqList");
	    }
	    System.out.println(7);
		return mav;
	}
	
	
	
	@RequestMapping("/faq/add")
	public ModelAndView addFaq(HttpServletRequest request, Model model) {
		
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		HashMap<String,Object> loginAdmin
			= (HashMap<String, Object>) session.getAttribute("loginAdmin");
		if (loginAdmin == null) {
			mav.setViewName("admin/admingLogin");
		} else {
			mav.setViewName("admin/faq/faqList");
		}
		return mav;
	}
	
	
	
	@RequestMapping(value="/faq/add/form",  method=RequestMethod.POST)
	public ModelAndView addFaqForm( @ModelAttribute("fdto") @Valid FaqDto faqdto,
			BindingResult result, HttpServletRequest request) {
		
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		HashMap<String,Object> loginUser
			= (HashMap<String, Object>) session.getAttribute("loginAdmin");
		if (loginUser == null) mav.setViewName("admin/admingLogin");
	    else {
	    	if(result.getFieldError("subject") != null) {
	    		mav.addObject("message", "제목을 입력하세요");
	    		mav.setViewName("faq/addFaq");
	    		return mav;
	    	} else if(result.getFieldError("content") != null) {
	    		mav.addObject("message", "내용을 입력하세요");
	    		mav.setViewName("faq/addFaq");
	    		return mav;
	    	}
	    	
	    	HashMap<String,Object> paramMap = new HashMap<>();
	    	paramMap.put("id", loginUser.get("USERID"));
	    	paramMap.put("subject", faqdto.getFaq_subject());
	    	paramMap.put("content", faqdto.getFaq_content());
	    	fqs.addFaq(paramMap);
	    	mav.setViewName("redirect:/admin/faqList");
	    }
		return mav;
	}
	
	
	
	@RequestMapping("/faq/edit/form")
	public ModelAndView editFaqForm(HttpServletRequest request, Model model) {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		HashMap<String,Object> loginAdmin
			= (HashMap<String, Object>) session.getAttribute("loginAdmin");
		HashMap<String,Object> paramMap = new HashMap();
		if (loginAdmin == null) mav.setViewName("admin/admingLogin");
	    else {
	    	fqs.faqEdit(paramMap);
	    	mav.setViewName("redirect:/admin/faqList");
	    }
		return mav;
	}
	
	
	
	@RequestMapping("/faq/delete")
	public String deleteFaq(	@RequestParam("faqnum") FaqDto faq_num) {
			
			HashMap<String,Object> paramMap = new HashMap<String,Object>();
			paramMap.put("faqnum", faq_num);
			fqs.deleteFaq(paramMap);
			
		return "redirect:/admin/faqList";
	}
	
	
	
	@RequestMapping("/admin/qnaList")
	public ModelAndView qnaList(HttpServletRequest request, Model model,  
			@ModelAttribute("qdto") @Valid QnaDto qnadto) {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		if(session.getAttribute("loginAdmin") == null) {
			mav.setViewName("admin/admingLogin");
		} else {
			int page = 1;
			String key = "";
			if(request.getParameter("first")!=null) {
				session.removeAttribute("page");
				session.removeAttribute("key");
			}
			if(request.getParameter("page")!=null) {
				page = Integer.parseInt(request.getParameter("page"));
				session.setAttribute("page", page);
			} else if(session.getAttribute("page") != null) {
				page = (Integer)session.getAttribute("page");
			} else {
				session.removeAttribute("page");
			}
			if(request.getParameter("key")!=null) {
				key = request.getParameter("key");
				session.setAttribute("key", key);
			} else if(session.getAttribute("key")!=null) {
				key = (String)session.getAttribute("key");
			} else {
				session.removeAttribute("key");
		}
			Paging paging = new Paging();
			paging.setPage(page);
			HashMap<String,Object> paramMap = new HashMap<>();
			System.out.println(qnadto.getQna_num());
			paramMap.put("qna_num", qnadto.getQna_num());
			paramMap.put("cnt", 0);
			paramMap.put("key", key);
			System.out.println(8);
			as.getAllCount(paramMap);
			System.out.println(9);
			int cnt = Integer.parseInt(paramMap.get("cnt").toString());
			paging.setTotalCount(cnt);
			paging.paging();
			System.out.println(10);
			
			System.out.println(paging.getStartNum());
			System.out.println(paging.getEndNum());
			System.out.println(key);
			paramMap.put("startNum", paging.getStartNum());
			paramMap.put("endNum", paging.getEndNum());
			paramMap.put("ref_cursor", null);
			fqs.adminQnaList(paramMap);
			
			ArrayList<HashMap<String,Object>> list
				= (ArrayList<HashMap<String, Object>>) paramMap.get("ref_cursor");
			System.out.println(11);
			mav.addObject("qnaList", list);
			mav.addObject("paging", paging);
			mav.setViewName("/admin/adminQna/qnaView");
		}
		return mav;
	}
		
	
	
	@RequestMapping("/qna/detail")
	public String qnaView() {
		return "";
	}
	

	
	@RequestMapping("/qna/reply")
	public String qnaReply() {
		return "adminQna/qna";
	}
	
	
	
	@RequestMapping("/qna")
	public String userQna(HttpServletRequest request) {
		HttpSession session = request.getSession();
		HashMap<String, Object> loginUser
			= (HashMap<String, Object>) session.getAttribute("loginUser");
		if(loginUser==null) return "member/login";
		else {
			return "userFaqQna/qna";
		}
	}
	
	
	
	@RequestMapping("/qna/add")
	public String addQna(HttpServletRequest request) {
		HttpSession session = request.getSession();
		HashMap<String, Object> loginUser
			= (HashMap<String, Object>) session.getAttribute("loginUser");
		if(loginUser==null) return "member/login";
		else {
			return "qna/qnaView";
		}
	}
	
	

	@RequestMapping("/qna/add/form")
	public ModelAndView addQnaForm( @ModelAttribute("qdto") @Valid QnaDto qnadto,
			BindingResult result, HttpServletRequest request) {
		
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		HashMap<String,Object> loginUser
			= (HashMap<String, Object>) session.getAttribute("loginUser");
		if (loginUser == null) mav.setViewName("member/login");
	    else {
	    	if(result.getFieldError("subject") != null) {
	    		mav.addObject("message", "제목을 입력하세요");
	    		mav.setViewName("qna/qna/add");
	    		return mav;
	    	} else if(result.getFieldError("content") != null) {
	    		mav.addObject("message", "내용을 입력하세요");
	    		mav.setViewName("qna/qna/add");
	    		return mav;
	    	}
	    	
	    	HashMap<String,Object> paramMap = new HashMap<>();
	    	paramMap.put("id", loginUser.get("USERID"));
	    	paramMap.put("subject", qnadto.getQna_subject());
	    	paramMap.put("content", qnadto.getQna_content());
	    	fqs.addQna(paramMap);
	    	mav.setViewName("redirect:/qna");
	    }
		return mav;
	}
	
	
	
	@RequestMapping("/qna/edit")
	public ModelAndView editQna(HttpServletRequest request, Model model) {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		HashMap<String,Object> loginUser
			= (HashMap<String, Object>) session.getAttribute("loginUser");
		HashMap<String,Object> paramMap = new HashMap();
		if (loginUser == null) mav.setViewName("member/login");
	    else {
	    	fqs.qnaEdit(paramMap);
	    	mav.setViewName("redirect:/qna/edit");
	    }
		return mav;
	}
	
	
	
	@RequestMapping("/qna/delete")
	public String deleteQna(@RequestParam("qna_num") QnaDto qna_num) {
		HashMap<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("qnanum", qna_num);
		fqs.deleteQna(paramMap);
		
		return "redirect:/qnaList";
	}
	
	
	
	@RequestMapping("/faqList")
	public String faqList() {
		return "userfaqqna/faq";
	}
}