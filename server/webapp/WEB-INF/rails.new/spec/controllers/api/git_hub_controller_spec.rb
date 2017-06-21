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
    @server_config_service = double('Server Config Service')
    controller.stub(:material_update_service).and_return(@material_update_service)
    controller.stub(:server_config_service).and_return(@server_config_service)
  end

  describe :notify do

    it "should validate the request contains X-Hub-Signature header" do
      # need to mock the request headers
      @server_config_service.should_receive(:getWebhookSecret).and_return("secret")
      params = { "repository" => { "full_name" => "org/repo", "html_url" => "https://github.com/org/repo"}, "ref" => "refs/heads/branch"}
      request.stub(:body) do
        StringIO.new(params.to_json)
      end
      signature = "sha1=" + OpenSSL::HMAC.hexdigest(OpenSSL::Digest.new('sha1'), "secret", request.body.read)
      request.headers["X-Hub-Signature"]= signature
      request.headers["Content-Type"]= "application/json"

      controller.stub(:prempt_ping_call)
      controller.stub(:allow_only_push_event)
      @material_update_service.should_receive(:updateGitMaterial).with("branch", ["https://github.com/org/repo", "https://github.com/org/repo.git", "http://github.com/org/repo", "http://github.com/org/repo.git", "git://github.com/org/repo", "git://github.com/org/repo.git", "git@github.com:org/repo", "git@github.com:org/repo.git"]).and_return(true)


      post :notify, params
      expect(response.status).to eq(202)
      expect(response.body).to eq("OK!")

    end

    it "should for url encoded" do

      params_inner = { "repository" => { "full_name" => "org/repo", "html_url" => "https://github.com/org/repo"}, "ref" => "refs/heads/branch"}
      params = {payload: params_inner.to_json}
      request.headers["content-type"] ="application/x-www-form-urlencoded"

      post :notify, params
    end

  end
end
