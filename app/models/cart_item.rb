# 购物车,数量
# and open the template in the editor.

class CartItem
  attr_reader :product, :quantity

  def initialize(product)
    @product = product
    @quantity = 1
  end

  def increment_quantity
    @quantity +=1
  end

  def title
    @product.title
  end

  def id
    @product.id
  end

  def now_price
    @product.now_price
  end

  def price
    @product.now_price * @quantity
  end
end
