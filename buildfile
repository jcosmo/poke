require 'buildr_plus'
BuildrPlus::Jenkins.auto_deploy = false
BuildrPlus::Checkstyle.modern_checkstyle_rule_type = true

require 'buildr_plus/projects/java_singlemodule'

BuildrPlus::Roles.project('poke') do
  project.comment = 'Poke: Hit a bearer-only secure endpoint and dump the output'
  project.group = 'poke'
  project.version = '0.1'

  compile.with BuildrPlus::Libs.keycloak,
               BuildrPlus::Libs.keycloak_authfilter,
               BuildrPlus::Libs.glassfish_embedded

  package(:jar).with(:manifest => 'src/main/etc/MANIFEST.MF')
end

require 'buildr_plus/activate'
