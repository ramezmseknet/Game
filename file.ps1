# --- One-Time Setup ---
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
    $status = git status --porcelain
    
    if ($status) {
        Write-Host "New changes detected. Updating repository..." -ForegroundColor Yellow
        
        # 1. STAGE FIRST (Fixes the 'unstaged changes' error in your screenshot)
        git add .
        
        # 2. PULL SECOND (Merges any remote changes into your staged work)
        git pull --rebase origin main
        
        # 3. SELECT MESSAGE
        $messages = @(
            "feat: add logic for assignment tasks",
            "fix: debug issues in main function",
            "docs: update comments and readme instructions",
            "refactor: clean up variable names for clarity",
            "feat: implement basic input handling",
            "style: format code according to course guidelines",
            "fix: correct logic error in loops",
            "feat: add final touches to lab exercise",
            "chore: prepare project for submission",
            "feat: complete initial implementation of project",
            "refactor: reorganize project structure",
            "fix: resolve runtime errors",
            "feat: update solution for lab requirements",
            "docs: clarify code documentation",
            "style: improve indentation and spacing"
        )
        $randomMessage = $messages | Get-Random
        
        # 4. COMMIT AND PUSH
        git commit -m "$randomMessage"
        git push origin main
        
        Write-Host "Successfully pushed: $randomMessage" -ForegroundColor Green
    } 

    Start-Sleep -Seconds 10
}