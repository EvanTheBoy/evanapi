package com.evan.evanapi.controller;

import com.evan.evanapi.common.BaseResponse;
import com.evan.evanapi.model.vo.InterfaceInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/analysis")
@Slf4j
public class AnalysisController {
    @GetMapping("/top/invoked/interface")
    public BaseResponse<List<InterfaceInfoVO>> listInvokedInterface() {

    }
}
