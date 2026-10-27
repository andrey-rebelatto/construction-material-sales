#!/usr/bin/env python3
"""Exporta páginas renderizadas pelo Spring como protótipos estáticos sem tokens CSRF."""
import argparse
from pathlib import Path
import re
import shutil
from urllib.request import urlopen

ROTAS = {
    '/': 'index.html',
    '/clientes': 'clientes.html',
    '/clientes/novo': 'cliente-novo.html',
    '/clientes/1/editar': 'cliente-editar.html',
    '/vendedores': 'vendedores.html',
    '/vendedores/novo': 'vendedor-novo.html',
    '/vendedores/1/editar': 'vendedor-editar.html',
    '/produtos': 'produtos.html',
    '/produtos/novo': 'produto-novo.html',
    '/produtos/1/editar': 'produto-editar.html',
    '/vendas': 'vendas.html',
    '/vendas/nova': 'venda-nova.html',
    '/vendas/1': 'venda-detalhe.html',
    '/vendas/1/editar': 'venda-editar.html',
    '/vendas/2': 'venda-detalhe-2.html',
    '/vendas/2/editar': 'venda-editar-2.html',
}


def destino(caminho):
    if caminho in ROTAS:
        return ROTAS[caminho]
    for plural, singular in [('clientes', 'cliente'), ('vendedores', 'vendedor'), ('produtos', 'produto')]:
        if re.fullmatch(r'/' + plural + r'/\d+/editar', caminho):
            return singular + '-editar.html'
    return caminho


def exportar(base):
    raiz = Path(__file__).resolve().parents[1]
    pasta = raiz / 'docs/prototipos'
    pasta.mkdir(parents=True, exist_ok=True)
    for rota, arquivo in ROTAS.items():
        with urlopen(base.rstrip('/') + rota) as resposta:
            html = resposta.read().decode('utf-8')
        html = re.sub(r'<input\b[^>]*name="_csrf"[^>]*>', '', html)
        html = re.sub(r'<meta\b[^>]*name="_csrf"[^>]*>', '', html)
        html = re.sub(r'href="([^"]+)"', lambda m: 'href="' + destino(m[1]) + '"', html)
        html = html.replace('href="/css/app.css"', 'href="assets/app.css"')
        html = html.replace('src="/js/app.js"', 'src="assets/app.js"')
        html = re.sub(r' action="[^"]*"', ' action="#"', html)
        html = html.replace('</head>', '<script src="assets/prototipo.js" defer></script></head>')
        html = html.replace('<main class="main">', '<main class="main"><div class="alert success">Protótipo de tela. Dados de demonstração; alterações não são salvas.</div>')
        html = '\n'.join(linha.rstrip() for linha in html.splitlines()) + '\n'
        (pasta / arquivo).write_text(html, encoding='utf-8')
    assets = pasta / 'assets'
    assets.mkdir(exist_ok=True)
    for origem, nome in [('css/app.css', 'app.css'), ('js/app.js', 'app.js')]:
        shutil.copyfile(raiz / 'src/main/resources/static' / origem, assets / nome)
    print(f'{len(ROTAS)} protótipos exportados para {pasta}')


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--url', default='http://localhost:8080')
    exportar(parser.parse_args().url)
