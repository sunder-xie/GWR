package com.gewara.web.action.subject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gewara.web.action.AnnotationController;

// ֩����ר�� ����
@Controller
public class SpiderManProxyController  extends AnnotationController {

	@RequestMapping("/admin/newsubject/spiderman.xhtml")
	public String spiderman(){
		return "admin/newsubject/spiderman.vm";
	}
	
	
}