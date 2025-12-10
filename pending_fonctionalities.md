 STEP 2 — Update TerrainPanel to notify window when score changes

(Only if you want score updates live)

Inside TerrainPanel, after a point is scored:

# window.refreshPlayerHUD();


But to do this, we pass the window reference:

Change constructor:

# public TerrainPanel(Terrain terrain, Players p1, Players p2, TerrainWindow window)


But for now, you can skip this until you add scoring.