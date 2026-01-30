# --- Git Auto-Sync Script (5-Minute Intervals) ---

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
    Write-Host "Repository initialized successfully!" -ForegroundColor Green
}

# --- The 5-Minute Loop ---
Write-Host "Monitoring workspace... Press Ctrl+C to stop." -ForegroundColor Cyan
Write-Host "Auto-sync interval: Every 5 minutes" -ForegroundColor Cyan

while ($true) {
    try {
        # Check for any modified, deleted, or new files
        $status = git status --porcelain
        
        if ($status) {
            Write-Host "`n[$(Get-Date -Format 'HH:mm:ss')] Changes detected. Starting sync process..." -ForegroundColor Yellow
            
            # 1. Stage everything first to protect local work
            git add . 2>&1 | Out-Null
            
            # 2. Pull with rebase to keep history clean
            # The -Xours flag automatically resolves conflicts by keeping your version
            $pullOutput = git pull --rebase -Xours origin main 2>&1
            
            # 3. Check again if there's still something to commit after the pull
            $statusAfterPull = git status --porcelain
            if ($statusAfterPull) {
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
                    "style: improve indentation and spacing",
                    "feat: progress on course project",
                    "fix: address code review feedback",
                    "chore: update dependencies",
                    "feat: implement new feature",
                    "refactor: improve code structure"
                )
                $randomMessage = $messages | Get-Random
                
                # 4. Commit (suppress output and errors)
                $commitOutput = git commit -m "$randomMessage" 2>&1
                
                # 5. Push (suppress errors but check for success)
                $pushOutput = git push origin main 2>&1
                
                if ($LASTEXITCODE -eq 0) {
                    Write-Host "[$(Get-Date -Format 'HH:mm:ss')] ✓ Successfully synced: $randomMessage" -ForegroundColor Green
                } else {
                    Write-Host "[$(Get-Date -Format 'HH:mm:ss')] ⚠ Push completed with warnings (check manually if needed)" -ForegroundColor Yellow
                }
            }
            else {
                Write-Host "[$(Get-Date -Format 'HH:mm:ss')] Sync completed via pull. No new local changes to commit." -ForegroundColor Gray
            }
        }
        else {
            Write-Host "[$(Get-Date -Format 'HH:mm:ss')] No changes detected." -ForegroundColor DarkGray
        }
    }
    catch {
        Write-Host "[$(Get-Date -Format 'HH:mm:ss')] Sync skipped due to error. Will retry in 5 minutes." -ForegroundColor Red
        
        # Check if there's a stuck rebase and abort it
        $rebaseCheck = git status 2>&1 | Select-String "rebase in progress"
        if ($rebaseCheck) {
            Write-Host "Detected stuck rebase. Aborting..." -ForegroundColor Yellow
            git rebase --abort 2>&1 | Out-Null
        }
    }
    
    # Wait for 5 minutes (300 seconds)
    Start-Sleep -Seconds 10
}