class AddOrderPhone < ActiveRecord::Migration
  def self.up
    add_column :orders, :phone, :string
    add_column :orders, :address, :string
  end

  def self.down
    remove_column :orders, :phone
    remove_column :orders, :address
  end
end
