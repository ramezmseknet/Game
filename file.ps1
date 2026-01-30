# --- One-Time Setup ---
# This block ensures the repository is initialized and connected to GitHub
if (!(Test-Path .git)) {
    Write-Host "Initializing new repository..." -ForegroundColor Green
    echo "# Programming Project" >> README.md
    git init
    git add README.md
    git commit -m "initial: repository setup"
    git branch -M main
    git remote add origin https://github.com/ramezmseknet/Game.git
    git push -u origin main
}

# --- The 20-Minute Loop ---
Write-Host "Monitoring workspace for changes... Press Ctrl+C to stop." -ForegroundColor Cyan

while($true) {
    # Check for any modified or new files
    $status = git status --porcelain
    
    if ($status) {
        Write-Host "New changes detected. Updating repository..." -ForegroundColor Yellow
        
        # 1. Sync with remote first (prevents push conflicts)
        git pull --rebase origin main
        
        # 2. Stage all files
        git add .
        
        # 3. Select a professional, simple message from the list
        $messages = @(
            "feat: add logic for assignment tasks",
            "fix: debug issues in main function",
            "docs: update comments and readme instructions",
            "refactor: clean up variable names for clarity",
            "feat: implement basic input handling",
            "style: format code according to course guidelines",
            "fix: correct logic error in loops",
            "chore: prepare project for submission",
            "feat: complete initial implementation of project",
            "fix: resolve runtime errors",
            "feat: update solution  requirements",
            "docs: clarify code documentation",
            "style: improve indentation and spacing"
        )
        $randomMessage = $messages | Get-Random
        
        # 4. Commit and Push
        git commit -m "$randomMessage"
        git push origin main
        
        Write-Host "Successfully pushed: $randomMessage" -ForegroundColor Green
    } else {
        # Optional: Print a small heart-beat to show the script is still running
        Write-Host "." -NoNewline
    }

    # Wait for 20 minutes (1200 seconds)
    Start-Sleep -Seconds 10
}