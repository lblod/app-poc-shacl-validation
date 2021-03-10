defmodule Dispatcher do
  use Matcher

  define_accept_types [
    html: ["text/html", "application/xhtml+html"],
    json: ["application/json", "application/vnd.api+json"],
    upload: ["multipart/form-data"],
    any: [ "*/*" ],
  ]

  define_layers [ :api, :frontend, :not_found ]

  ###############################################################
  # domain.json
  ###############################################################
  match "/validation-reports/*path", %{ accept: [:json], layer: :api} do
    Proxy.forward conn, path, "http://resource/validation-reports/"
  end

  match "/validation-results/*path", %{ accept: [:json], layer: :api} do
    Proxy.forward conn, path, "http://resource/validation-results/"
  end

  match "/severity/*path", %{ accept: [:json], layer: :api} do
    Proxy.forward conn, path, "http://resource/severity/"
  end

  match "/shape/*path", %{ accept: [:json], layer: :api} do
    Proxy.forward conn, path, "http://resource/shape/"
  end

  match "/constraint-component/*path", %{ accept: [:json], layer: :api} do
    Proxy.forward conn, path, "http://resource/constraint-component/"
  end
end


