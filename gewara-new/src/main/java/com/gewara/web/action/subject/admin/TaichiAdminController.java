package com.gewara.web.action.subject.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.web.action.AnnotationController;

// ̫��ר�� ����
@Controller
public class TaichiAdminController  extends AnnotationController {

	
	@RequestMapping("/admin/newsubject/taichi.xhtml")
	public String taichi(){
		return "admin/newsubject/taichi.vm";
	}
	
}