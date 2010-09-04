xml.instruct!(:xml,:version=>'1.0')
xml.rss :version=>"2.0" do
  xml.channel do
    xml.title "上团购三，淘每日团购精品"
    xml.item do
      xml.title @product.title
      xml.description @product.summary
      xml.details @product.details
      xml.created_at @product.created_at
    end
  end
end