import xml.etree.ElementTree as ET

def generate_tmx(tile_types, map_width, map_height, tile_width, tile_height, hex_side_length, stagger_axis, stagger_index, output_file):
    map_element = ET.Element("map", {
        "version": "1.10",
        "tiledversion": "1.10.2",
        "orientation": "hexagonal",
        "renderorder": "right-down",
        "width": str(map_width),
        "height": str(map_height),
        "tilewidth": str(tile_width),
        "tileheight": str(tile_height),
        "infinite": "0",
        "hexsidelength": str(hex_side_length),
        "staggeraxis": stagger_axis,
        "staggerindex": stagger_index,
        "nextlayerid": "2",
        "nextobjectid": "1"
    })

    tileset = ET.SubElement(map_element, "tileset", {
        "firstgid": "1",
        "source": "please-best.tsx"
    })

    layer = ET.SubElement(map_element, "layer", {
        "id": "1",
        "name": "Tile Layer 1",
        "width": str(map_width),
        "height": str(map_height)
    })

    data = ET.SubElement(layer, "data", {
        "encoding": "csv"
    })

    tile_data = [[0 for _ in range(map_width)] for _ in range(map_height)]

    for y, row in enumerate(tile_types):
        for x, tile in enumerate(row):
            tile_data[y][x] = tile

    flattened_data = []
    for row in tile_data:
        flattened_data.extend(row)

    data.text = "\n" + ",".join(map(str, flattened_data)) + "\n"

    tree = ET.ElementTree(map_element)
    tree.write(output_file, encoding="utf-8", xml_declaration=True)

# Row for row. 
# Randomly generate a board by choosing 0 for water 1..6 for tiles. 
# Could then use a visual tool to click on the rows we want to generate a custom tileset
tile_types = [
    [1, 2],
    [2, 1]
]

generate_tmx(tile_types, map_width=30, map_height=20, tile_width=112, tile_height=85, hex_side_length=50, stagger_axis="y", stagger_index="odd", output_file="generated_map.tmx")
