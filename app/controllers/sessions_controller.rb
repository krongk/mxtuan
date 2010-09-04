# coding: utf-8  
class SessionsController < ApplicationController
  # Be sure to include AuthenticationSystem in Application Controller instead
  # render new.rhtml
  def new
  end

  def create
    self.current_user = User.authenticate(params[:login], params[:password])
    if logged_in?
      if params[:remember_me] == "1"
        current_user.remember_me unless current_user.remember_token?
        cookies[:auth_token] = { :value => self.current_user.remember_token , :expires => self.current_user.remember_token_expires_at }
      end
      redirect_back_or_default(session[:back_to] || '/')
      flash[:notice] = "登录成功！"
    else
      flash[:notice] = "用户名或密码错误,请检查"
      render :action => 'new'
    end
  end

  def destroy
    self.current_user.forget_me if logged_in?
    cookies.delete :auth_token
    reset_session
    flash[:notice] = "安全退出."
    redirect_back_or_default('/')
  end
end
