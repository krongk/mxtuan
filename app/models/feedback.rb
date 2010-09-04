class Feedback < ActiveRecord::Base
  validates_presence_of :message, :message=>"不能为空！"
end
