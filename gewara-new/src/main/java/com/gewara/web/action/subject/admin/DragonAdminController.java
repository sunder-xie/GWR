package com.gewara.web.action.subject.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.web.action.AnnotationController;

// ��֮��ר�� ����
@Controller
public class DragonAdminController  extends AnnotationController {

	
	@RequestMapping("/admin/newsubject/dragon.xhtml")
	public String dargon(){
		return "admin/newsubject/dragon.vm";
	}
	
}