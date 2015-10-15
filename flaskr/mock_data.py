HOST = "10.0.2.2"

enterprises = [
    {
        "name": "Universidad Catolica San Pablo",
        "category": "Universidad",
        "img": "http://%s:5000/static/ucsp.jpg" % (HOST)
    },
    {
        "name": "Plan H",
        "category": "Restaurante",
        "img": "http://%s:5000/static/uscp.jpg" % (HOST)
    }
]

quejas = [
    {
        "complain": "No hay papel en los banios",
        "enterprise_id": 1
    },
    {
        "complain": "Los wachimanes no revisan mi carnet al ingresar",
        "enterprise_id": 1
    },
    {
        "complain": "No insisten en llamarme cuando mi salchipapa esta lista",
        "enterprise_id": 2
    }
]

