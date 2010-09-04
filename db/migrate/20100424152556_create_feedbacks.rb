class CreateFeedbacks < ActiveRecord::Migration
  def self.up
    create_table :feedbacks do |t|
      t.string :name
      t.string :contact
      t.text :message
      t.boolean :is_check, :default=>'1'
      t.text :note

      t.timestamps
    end
  end

  def self.down
    drop_table :feedbacks
  end
end
