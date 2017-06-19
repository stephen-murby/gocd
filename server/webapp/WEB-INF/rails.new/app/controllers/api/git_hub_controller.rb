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

class Api::GitHubController < Api::ApiController

  def notify

    # validate the request loosely originates from GitHub using the 'X-GiHub-Delivery' header.
    if

      # use the existing service to notify associated materials of update
      result = HttpLocalizedOperationResult.new
      material_update_service.notifyMaterialsForUpdate(current_user, params, result)
      self.response.headers['Content-Type'] = 'text/plain; charset=UTF-8'
      render_localized_operation_result result
    else
      # return bad request if the header was missing. (not strictly un-authorised, but we wouldn't want to perform the update)
    end

  end
end