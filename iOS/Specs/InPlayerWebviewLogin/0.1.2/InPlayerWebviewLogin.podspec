Pod::Spec.new do |s|
  s.name             = "InPlayerWebviewLogin"
  s.version          = '0.1.0'
  s.summary          = "InPlayerWebviewLogin"
  s.description      = <<-DESC
                        InPlayerWebviewLogin.
                       DESC
  s.homepage         = "https://github.com/applicaster/InPlayerWebviewLogin-iOS"
  s.license          = 'CMPS'
  s.author           = { "cmps" => "p.rueda@applicaster.com" }
  s.source           = { :git => "git@github.com:applicaster/InPlayerWebviewLogin-iOS.git", :tag => s.version.to_s }
  s.platform     = :ios, '10.0'
  s.requires_arc = true
  s.static_framework = true

  s.public_header_files = 'InPlayerWebviewLogin/**/*.h'
  s.source_files = 'InPlayerWebviewLogin/**/*.{h,m,swift}'

  s.resources = [ "InPlayerWebviewLogin/**/*.xib",
                  "InPlayerWebviewLogin/**/*.png"]

  s.xcconfig =  { 
                  'CLANG_ALLOW_NON_MODULAR_INCLUDES_IN_FRAMEWORK_MODULES' => 'YES',
                  'ENABLE_BITCODE' => 'YES'
                }                  

  s.dependency 'ZappPlugins'
  s.dependency 'ZappLoginPluginsSDK'

end
