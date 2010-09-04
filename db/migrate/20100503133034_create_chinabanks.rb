class CreateChinabanks < ActiveRecord::Migration
  def self.up
    create_table :chinabanks do |t|

      t.timestamps
    end
  end

  def self.down
    drop_table :chinabanks
  end
end
