HOST = "10.0.2.2"

enterprises = [
    {
        "id": 1,
        "name": "Universidad Catolica San Pablo",
        "category": "Universidad",
        "img": "http://%s:5000/static/ucsp.jpg" % (HOST)
    },
    {
        "id": 2,
        "name": "Plan H",
        "category": "Restaurante",
        "img": "http://%s:5000/static/uscp.jpg" % (HOST)
    }
]

complains = [
    {
        "title": "Papel",
        "detail": "No hay papel en los banios",
        "enterprise_id": 1
    },
    {
        "title": "Carnet",
        "detail": "Los wachimanes no revisan mi carnet al ingresar",
        "enterprise_id": 1
    },
    {
        "title": "No llaman",
        "detail": "No insisten en llamarme cuando mi salchipapa esta lista",
        "enterprise_id": 2
    }
]

