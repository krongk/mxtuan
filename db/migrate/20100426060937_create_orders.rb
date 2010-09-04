class CreateOrders < ActiveRecord::Migration
  def self.up
    create_table :orders do |t|
      t.references :user
      t.references :product
      t.integer :quantity
      t.float :discount
      t.float :order_price
      t.boolean :is_pay
      t.integer :pay_type

      t.timestamps
    end
  end

  def self.down
    drop_table :orders
  end
end
