package org.ibase4j.web.sys;

import org.apache.shiro.SecurityUtils;
import org.ibase4j.core.config.Resources;
import org.ibase4j.core.support.HttpCode;
import org.ibase4j.core.support.shiro.LoginHelper;
import org.ibase4j.core.util.Request2ModelUtil;
import org.ibase4j.mybatis.generator.model.SysUser;
import org.ibase4j.service.SysUserService;
import org.ibase4j.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 用户登录
 * 
 * @author ShenHuaJie
 */
@Controller
public class LoginController extends BaseController {
	@Autowired
	private SysUserService sysUserService;

	// 登录
	@ResponseBody
	@RequestMapping(value = "/login")
	public ModelMap login(@RequestParam(value = "account", required = false) String account,
			@RequestParam(value = "password", required = false) String password) {
		Assert.notNull(account, Resources.getMessage("ACCOUNT_IS_NULL"));
		Assert.notNull(password, Resources.getMessage("PASSWORD_IS_NULL"));
		if (LoginHelper.login(account, sysUserService.encryptPassword(password))) {
			return setSuccessModelMap();
		}
		throw new IllegalArgumentException(Resources.getMessage("LOGIN_FAIL"));
	}

	// 登出
	@ResponseBody
	@RequestMapping("/logout")
	public ModelMap logout() {
		SecurityUtils.getSubject().logout();
		return setSuccessModelMap();
	}

	// 注册
	@ResponseBody
	@RequestMapping(value = "/regin")
	public ModelMap regin(@RequestParam(value = "account", required = false) String account,
			@RequestParam(value = "password", required = false) String password) {
		SysUser sysUser = Request2ModelUtil.covert(SysUser.class, request);
		sysUserService.addUser(sysUser);
		if (LoginHelper.login(account, password)) {
			return setSuccessModelMap();
		}
		throw new IllegalArgumentException(Resources.getMessage("LOGIN_FAIL"));
	}

	// 没有登录
	@ResponseBody
	@RequestMapping("/unauthorized")
	public ModelMap unauthorized() {
		SecurityUtils.getSubject().logout();
		return setModelMap(HttpCode.UNAUTHORIZED);
	}

	// 没有权限
	@ResponseBody
	@RequestMapping("/forbidden")
	public ModelMap forbidden() {
		return setModelMap(HttpCode.FORBIDDEN);
	}
}