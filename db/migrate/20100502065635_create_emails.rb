class CreateEmails < ActiveRecord::Migration
  def self.up
    create_table :emails do |t|
      t.integer :city
      t.string :mail
      t.boolean :is_ok,:default=>'1'
      t.string :note

      t.timestamps
    end
  end

  def self.down
    drop_table :emails
  end
end
