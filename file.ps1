# --- One-Time Setup ---
# Only runs if the .git folder doesn't exist yet
if (!(Test-Path .git)) {
    echo "# Game" >> README.md
    git init
    git add README.md
    git commit -m "first commit"
    git branch -M main
    git remote add origin https://github.com/ramezmseknet/Game.git
    git push -u origin main
}

# --- The 20-Minute Loop ---
Write-Host "Starting auto-commit loop... Press Ctrl+C to stop." -ForegroundColor Cyan
while($true) {
    $status = git status --porcelain
    if ($status) {
        $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
        Write-Host "[$timestamp] Syncing and pushing to GitHub..." -ForegroundColor Yellow
        
        # 1. Pull latest changes (rebase keeps history clean)
        git pull --rebase origin main
        
        # 2. Add and commit
        git add .
        git commit -m "Auto-commit: $timestamp"
        
        # 3. Push
        git push origin main
    } else {
        Write-Host "[$($timestamp = Get-Date -Format 'HH:mm:ss')] No changes detected. Skipping." -ForegroundColor Gray
    }

    Start-Sleep -Seconds 1200
}