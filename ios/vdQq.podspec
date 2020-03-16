

Pod::Spec.new do |s|



  s.name         = "vdQq"
  s.version      = "1.0.0"
  s.summary      = "vd plugin."
  s.description  = <<-DESC
                    vd plugin.
                   DESC

  s.homepage     = "https://vd.app"
  s.license      = "MIT"
  s.author             = { "ViewDesign" => "viewdesign@gmail.com" }
  s.source =  { :path => '.' }
  s.source_files  = "vdQq", "**/**/*.{h,m,mm,c}"
  s.exclude_files = "Source/Exclude"
  s.vendored_frameworks = 'vdQq/*.framework'
  s.resources = 'vdQq/resources/*.*'
  s.platform     = :ios, "8.0"
  s.requires_arc = true

  s.dependency 'WeexSDK'
  s.dependency 'vd'
  s.dependency 'WeexPluginLoader', '~> 0.0.1.9.1'

end
