##########################################################################
# Copyright 2017 ThoughtWorks, Inc.
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
##########################################################################

module ApiV4
  module Admin
    module Pipelines
      class PipelineConfigRepresenter < BaseRepresenter
        alias_method :pipeline, :represented

        error_representer({
                            'labelTemplate' => 'label_template',
                            'params' => 'parameters',
                            'variables' => 'environment_variables',
                            'trackingTool' => 'tracking_tool'
                          })

        link :self do |opts|
          opts[:url_builder].apiv4_admin_pipeline_url(pipeline_name: pipeline.name) if pipeline.errors().isEmpty()
        end

        link :doc do |opts|
          'https://api.gocd.org/#pipeline-config'
        end

        link :find do |opts|
          opts[:url_builder].apiv4_admin_pipeline_url(pipeline_name: '__pipeline_name__').gsub(/__pipeline_name__/, ':pipeline_name')
        end

        property :label_template
        property :enable_pipeline_locking,
                 getter: lambda { |options| self.isLock },
                 setter: lambda { |val, options| val ? self.lockExplicitly : self.unlockExplicitly }

        property :name, case_insensitive_string: true
        property :template_name, as: :template, case_insensitive_string: true

        property :origin,
                 decorator: lambda { |origin, *|
                   if origin.instance_of? FileConfigOrigin
                     Shared::ConfigOrigin::ConfigXmlOriginRepresenter
                   else
                     Shared::ConfigOrigin::ConfigRepoOriginRepresenter
                   end
                 },
                 skip_parse: true

        collection :parameters,
                   exec_context: :decorator,
                   decorator: ParamRepresenter,
                   expect_hash: true,
                   class: com.thoughtworks.go.config.ParamConfig

        collection :environment_variables,
                   exec_context: :decorator,
                   decorator: Shared::EnvironmentVariableRepresenter,
                   expect_hash: true,
                   class: com.thoughtworks.go.config.EnvironmentVariableConfig

        collection :materials,
                   exec_context: :decorator,
                   decorator: Admin::Pipelines::Materials::MaterialRepresenter,
                   expect_hash: true,
                   class: lambda { |fragment, *|
                     Admin::Pipelines::Materials::MaterialRepresenter.get_material_type(fragment[:type]||fragment['type'])
                   }
        collection :stages,
                   exec_context: :decorator,
                   decorator: Shared::Stages::StageRepresenter,
                   expect_hash: true,
                   class: com.thoughtworks.go.config.StageConfig

        property :tracking_tool,
                 exec_context: :decorator,
                 decorator: Admin::Pipelines::TrackingTool::TrackingToolRepresenter,
                 expect_hash: true,
                 class: lambda { |object, *|
                   Admin::Pipelines::TrackingTool::TrackingToolRepresenter.get_class(object[:type] || object['type'])
                 }

        property :timer,
                 decorator: Admin::Pipelines::TimerRepresenter,
                 class: com.thoughtworks.go.config.TimerConfig,
                 skip_parse: SkipParseOnBlank

        delegate :name, :name=, to: :pipeline

        def parameters
          pipeline.params
        end

        def parameters=(new_params)
          pipeline.params = new_params
        end

        def environment_variables
          pipeline.getVariables()
        end

        def environment_variables=(array_of_variables)
          pipeline.setVariables(EnvironmentVariablesConfig.new(array_of_variables))
        end

        def materials
          pipeline.materialConfigs()
        end

        def materials=(value)
          pipeline.materialConfigs().clear
          value.each { |material| pipeline.materialConfigs().add(material) }
        end

        def stages
          pipeline.getStages() if !pipeline.getStages().isEmpty
        end

        def stages=(value)
          pipeline.getStages().clear()
          value.each { |stage| pipeline.addStageWithoutValidityAssertion(stage) }
        end

        def tracking_tool
          if pipeline.getTrackingTool()
            pipeline.getTrackingTool()
          elsif pipeline.getMingleConfig().isDefined()
            pipeline.getMingleConfig()
          end

        end

        def tracking_tool=(value)
          if value.instance_of? com.thoughtworks.go.config.MingleConfig
            pipeline.setMingleConfig(value)
          elsif value.instance_of? com.thoughtworks.go.config.TrackingTool
            pipeline.setTrackingTool(value)
          end
        end

        def errors_with_material_config_errors
          pipeline.errors.addAll(pipeline.materialConfigs.errors)

          errors_without_material_config_errors
        end

        alias_method_chain :errors, :material_config_errors
      end
    end
  end
end