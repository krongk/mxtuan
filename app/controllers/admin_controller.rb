class AdminController < ApplicationController
  before_filter :admin_required
  layout "admin"
  def users
    @users = User.all(:order=>"created_at DESC")
  end

  def products
   redirect_to (products_path)
  end

  def orders
    @orders =Order.paginate :order=>"created_at DESC", :page=>params[:page],:per_page=>20
  end

  def sellers
    @sellers = Seller.paginate :order=>'created_at DESC',:page=>params[:page],:per_page=>10
  end

  def feedbacks
    @feedbacks = Feedback.paginate :order=>'created_at DESC',:page=>params[:page],:per_page=>10
  end

end
