class CreateSellers < ActiveRecord::Migration
  def self.up
    create_table :sellers do |t|
      t.string :name
      t.string :contact
      t.text :info
      t.boolean :is_front,  :default=>'1'
      t.boolean :is_checked,:default=>'0'
      t.text :note

      t.timestamps
    end
  end

  def self.down
    drop_table :sellers
  end
end
