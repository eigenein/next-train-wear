#!/usr/bin/env python3

import datetime
import itertools
import os
from pathlib import Path
from xml.etree import ElementTree

import click
import requests


HEADER = f'''
package me.eigenein.nexttrainwear.data

/**
 * Station catalogue.
 * Auto-generated on {datetime.datetime.now().replace(microsecond=0)}.
 */
object Stations {{

    val AMSTERDAM_CENTRAAL = Station("ASD", "Amsterdam Centraal", "NL", 52.3788871765137, 4.90027761459351)
    val ALL_STATIONS = arrayOf(
        AMSTERDAM_CENTRAAL
'''.strip('\r\n')

FOOTER = '''
    )
    val STATION_BY_CODE = ALL_STATIONS.map { Pair(it.code, it) }.toMap()
}'''


@click.command()
@click.option('-u', '--user', required=True, help='NS API username')
@click.option('-p', '--password', required=True, prompt=True, hide_input=True, help='NS API password')
def main(user, password):
    """Update station catalogue from NS API."""

    response = requests.get('http://webservices.ns.nl/ns-api-stations-v2', auth=(user, password))
    response.raise_for_status()

    catalogue_path = Path(__file__).parent / 'app' / 'src' / 'main' / 'java' / 'me' / 'eigenein' / 'nexttrainwear' / 'data' / 'Stations.kt'
    with catalogue_path.open('wt', encoding='utf-8') as catalogue_file:
        print(HEADER, file=catalogue_file, end='')

        stations_element = ElementTree.fromstring(response.content)
        for station_element in stations_element:
            code = station_element.find('Code').text
            if code == 'ASD':
                continue  # it's put in the header
            land = station_element.find('Land').text
            if land != 'NL':
                continue  # skip stations outside The Netherlands
            long_name = station_element.find('Namen').find('Lang').text
            latitude = station_element.find('Lat').text
            longitude = station_element.find('Lon').text
            print(f',{os.linesep}        Station("{code}", "{long_name}", "{land}", {latitude}, {longitude})', file=catalogue_file, end='')

        print(FOOTER, file=catalogue_file)


if __name__ == '__main__':
    main()
