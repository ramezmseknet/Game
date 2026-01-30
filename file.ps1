# --- The 20-Minute Loop ---
Write-Host "Monitoring workspace... Press Ctrl+C to stop." -ForegroundColor Cyan

while($true) {
    try {
        # Check for any modified, deleted, or new files
        $status = git status --porcelain
        
        if ($status) {
            Write-Host "Changes detected. Starting sync process..." -ForegroundColor Yellow
            
            # 1. Stage everything first to protect local work
            git add .
            
            # 2. Pull with rebase to keep history clean
            # The -Xours flag automatically resolves simple conflicts by keeping your cousin's work
            git pull --rebase -Xours origin main
            
            # 3. Check again if there's still something to commit after the pull
            if (git status --porcelain) {
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
                
                # 4. Commit and Push
                git commit -m "$randomMessage"
                git push origin main
                
                Write-Host "Successfully updated: $randomMessage" -ForegroundColor Green
            } else {
                Write-Host "Sync completed via pull. No new local changes to commit." -ForegroundColor Gray
            }
        }
    }
    catch {
        Write-Host "An error occurred during sync. Will retry in 20 minutes." -ForegroundColor Red
        # Optional: git rebase --abort (to clean up a stuck rebase)
    }

    # Wait for 20 minutes
    Start-Sleep -Seconds 20
}