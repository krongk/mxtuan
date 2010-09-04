#coding: utf-8
require 'nokogiri'
require 'open-uri'
require 'iconv'
class WelcomeController < ApplicationController
  #before_filter :login_required ,:only=>[:add_to_cart]
  def today
    @product = Product.find(:first,:conditions=>"status=1", :order=>'created_at desc')
  end

  def tomorrow
  end

  def yesterday
    @products = Product.paginate :order=>"created_at DESC", :page=>params[:page],:per_page=>8
  end

  def profile
    if logged_in?
      redirect_to user_path(current_user)
    else
      redirect_to '/login'
    end
  end
  #添加到购物车
  def add_to_cart
    session[:cart] = nil
    if logged_in?
      begin
        product = Product.find(params[:id])
      rescue ActiveRecord::RecordNotFound
        logger.error("试图访问无效的产品编号:#{params[:id]}")
        flash[:notice]="无效的产品,请检查!"
        redirect_to :action=>:today
      else
        @cart = find_cart
        @cart.add_product(product)
      end
    else
      flash[:notice]="只有注册用户才享有购买功能! 请先登录!"
      session[:back_to]="/welcome/add_to_cart/#{params[:id]}"
      redirect_to '/login'
    end
  end
  #清空购物车
  def empty_cart
    session[:cart] = nil
    flash[:notice]="购物车已清空"
    redirect_to :action => :today
  end
  #添加到订单
  def add_to_order
    @order = Order.new
    @order.user = current_user
    @order.product = Product.find(params[:product_id])
    @order.order_price = params[:order_price]
    @order.phone = params[:phone]
    if @order.save
      update_product(@order)
      flash[:notice]="下订单成功!支付以后,程序自动为你生成消费券."
      redirect_to edit_order_path(@order)
    else
      flash[:notice]="订单未成功,请检查"
      redirect_to "/add_to_cart"
    end
  end
  #生成订单后,更新团品数量
  def update_product(order)
    p = order.product
    p.sold_count+=1
    if p.save
      flash[:notice]="下订单成功!支付以后,程序自动为你生成消费券."
    end
  end
  #------------------------------------------
  private
  def find_cart
    session[:cart] ||= Cart.new
  end
end
