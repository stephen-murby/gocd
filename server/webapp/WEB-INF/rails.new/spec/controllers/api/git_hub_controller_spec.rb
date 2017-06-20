##########################GO-LICENSE-START################################
# Copyright 2014 ThoughtWorks, Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
##########################GO-LICENSE-END##################################

require 'spec_helper'

describe Api::GitHubController do
  include APIModelMother

  before :each do
    @material_update_service = double('Material Update Service')
    @user = Username.new(CaseInsensitiveString.new('go_user'))
    controller.stub(:current_user).and_return(@user)
    controller.stub(:material_update_service).and_return(@material_update_service)
    @params = {:post_commit_hook_material_type => 'GitHub', :no_layout => true, :payload => { :event => 'push'}}
  end

  describe :notify do

    it "should validate the request contains X-GitHub-Delivery header" do
      # need to mock the request object.
    end

    it "should return 401 when request does not contain required header" do
      post :notify, @params
      expect(response.status).to eq(401)
      expect(response.body).to eq("Request has not come from GitHub\n")
    end

  end

end
