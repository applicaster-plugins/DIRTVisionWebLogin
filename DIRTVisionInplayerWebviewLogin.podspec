Pod::Spec.new do |s|
  s.name             = "DIRTVisionInplayerWebviewLogin"
  s.version          = '0.1.1'
  s.summary          = "DIRTVisionInplayerWebviewLogin"
  s.description      = <<-DESC
                        DIRTVisionInplayerWebviewLogin.
                       DESC
  s.homepage         = "https://github.com/applicaster-plugins/DIRTVisionWebLogin"
  s.license          = 'CMPS'
  s.author           = { "cmps" => "p.rueda@applicaster.com" }
  s.source           = { :git => "git@github.com:applicaster-plugins/DIRTVisionWebLogin.git", :tag => s.version.to_s }
  s.platform     = :ios, '10.0'
  s.requires_arc = true
  s.static_framework = true

  s.public_header_files = 'DIRTVisionInplayerWebviewLogin/**/*.h'
  s.source_files = 'DIRTVisionInplayerWebviewLogin/**/*.{h,m,swift}'

  s.resources = [ "DIRTVisionInplayerWebviewLogin/**/*.xib",
                  "DIRTVisionInplayerWebviewLogin/**/*.png"]

  s.xcconfig =  { 
                  'CLANG_ALLOW_NON_MODULAR_INCLUDES_IN_FRAMEWORK_MODULES' => 'YES',
                  'ENABLE_BITCODE' => 'YES'
                }                  

  s.dependency 'ZappPlugins'
  s.dependency 'ZappLoginPluginsSDK'

end
