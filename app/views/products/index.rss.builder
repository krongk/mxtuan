xml.instruct!(:xml,:version=>'1.0')
xml.rss :version=>"2.0" do
  xml.channel do
    xml.title "团购3，每日团品展示"
    xml.description "每日团品展示"
    xml.link formatted_products_url(:rss)

    for product in @products
      xml.item do
        xml.title product.title
        xml.summary product.summary
        xml.created_at product.created_at
        xml.link formatted_product_url(product,:rss)
      end
    end
  end
end