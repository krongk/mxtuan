class CreateProducts < ActiveRecord::Migration
  def self.up
    create_table :products do |t|
      t.string :title
      t.integer :total_count
      t.integer :base_count
      t.integer :sold_count,  :default=>0
      t.float :original_price
      t.float :discount
      t.float :now_price
      t.float :save_price
      t.datetime :started_at, :default=>Time.now
      t.datetime :ended_at,   :default=>Time.now+24.hours
      t.string :picture
      t.text :summary
      t.text :details
      t.text :contact
      t.text :comment
      t.integer :status,    :default=>'1'
      t.boolean :is_publish,:default=>'1'
      t.boolean :is_ok,     :default=>'1'

      t.timestamps
    end
  end

  def self.down
    drop_table :products
  end
end
