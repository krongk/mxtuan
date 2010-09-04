class ChinabanksController < ApplicationController
 
  # GET /chinabanks
  # GET /chinabanks.xml
  def index
   
  end

  # GET /chinabanks/1
  # GET /chinabanks/1.xml
  def show
    @chinabank = Chinabank.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.xml  { render :xml => @chinabank }
    end
  end

  # GET /chinabanks/new
  # GET /chinabanks/new.xml
  def new
  
  end

  # GET /chinabanks/1/edit
  def edit
    @chinabank = Chinabank.find(params[:id])
  end

  # POST /chinabanks
  # POST /chinabanks.xml
  def create
    @chinabank = Chinabank.new(params[:chinabank])

    respond_to do |format|
      if @chinabank.save
        flash[:notice] = 'Chinabank was successfully created.'
        format.html { redirect_to(@chinabank) }
        format.xml  { render :xml => @chinabank, :status => :created, :location => @chinabank }
      else
        format.html { render :action => "new" }
        format.xml  { render :xml => @chinabank.errors, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /chinabanks/1
  # PUT /chinabanks/1.xml
  def update
    @chinabank = Chinabank.find(params[:id])

    respond_to do |format|
      if @chinabank.update_attributes(params[:chinabank])
        flash[:notice] = 'Chinabank was successfully updated.'
        format.html { redirect_to(@chinabank) }
        format.xml  { head :ok }
      else
        format.html { render :action => "edit" }
        format.xml  { render :xml => @chinabank.errors, :status => :unprocessable_entity }
      end
    end
  end

  # DELETE /chinabanks/1
  # DELETE /chinabanks/1.xml
  def destroy
    @chinabank = Chinabank.find(params[:id])
    @chinabank.destroy

    respond_to do |format|
      format.html { redirect_to(chinabanks_url) }
      format.xml  { head :ok }
    end
  end
end
