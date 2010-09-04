class AboutController < ApplicationController
  def index
  end

  def learn
  end

  def faq
  end

  def guide
    
  end
  def job
  end

  def maillist
    if session[:mail]

    else
      redirect_to "/emails/new"
    end
  end

  def press
  end

  def privacy
  end

end
