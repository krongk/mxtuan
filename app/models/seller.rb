class Seller < ActiveRecord::Base
  validates_presence_of :contact,:info,   :message=>"不能为空！"
end
