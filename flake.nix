{ 
description = "Flake to manage my Java workspace.";

inputs.nixpkgs.url = "nixpkgs/nixpkgs-unstable";

outputs = inputs: 
let
  system = "x86_64-linux";
  pkgs = inputs.nixpkgs.legacyPackages.${system};
in { 
  devShell.${system} = pkgs.mkShell rec {
    name = "java-shell";
    buildInputs = with pkgs; [ 
       (maven.override {
          jdk = jdk11;
       })
       jdk11
    ];
    
    shellHook = ''
      export JAVA_HOME=${pkgs.jdk11}
      PATH="${pkgs.jdk11}/bin:$PATH"
    '';
  };
 };
}
