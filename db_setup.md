# Database Setup & Sharing Guide

This document explains how to **export** your local PostgreSQL database on macOS, share it with a teammate on Windows, and **import** it on the Windows machine.

---

## 📦 Prerequisites

| Platform | Required Software |
|----------|-------------------|
| **macOS** | • PostgreSQL client (`psql`, `pg_dump`, `pg_restore`). Install via Homebrew: `brew install postgresql`<br>• `gzip` (bundled with macOS) for optional compression.<br>• Optional: `gpg` if you want to encrypt the dump before sharing.
| **Windows** | • PostgreSQL installer (includes `psql`, `pg_dump`, `pg_restore`). Download from <https://www.postgresql.org/download/windows/>.<br>• `gzip` utility – you can use the built‑in `tar` command on recent Windows 10/11 or install **7‑Zip** for `.gz` files.<br>• Optional: **Gpg4win** for decryption if the dump was encrypted.

---

## � Obtain the Dump File

1. The dump file will be uploaded to a shared Google Drive (or any cloud storage) by the project maintainer.
2. Download the file to a convenient location on your machine, e.g. `~/Downloads/tminder_backup.dump` (or `tminder_backup.dump.gz`).

## �🖥️ Import the Dump on macOS

```bash
# Ensure PostgreSQL client tools are installed
brew install postgresql   # if not already present

# (Optional) If the file is compressed, unzip it first
# tar -xzf ~/Downloads/tminder_backup.dump.gz -C ~/Downloads

# Create a new empty database (choose any name you like)
createdb -U <pg_user> -h localhost -p 5432 tminder_dev

# Restore the dump into the new database
pg_restore -U <pg_user> -h localhost -p 5432 -d tminder_dev -Fc ~/Downloads/tminder_backup.dump
```

Replace `<pg_user>` with your PostgreSQL user (often `postgres`). After the command finishes, the database will contain all tables and data.

---

```bash
# 1. Ensure PostgreSQL client is available
brew install postgresql   # if not already installed

# 2. Export the entire database (replace <db_name> and <pg_user>)
pg_dump -Fc -h localhost -U <pg_user> -d <db_name> -f tminder_backup.dump

# 3. (Optional) Compress the dump to make it smaller for transfer
gzip -c tminder_backup.dump > tminder_backup.dump.gz

# 4. Verify the dump (optional)
pg_restore --list -f - tminder_backup.dump
```

The resulting `tminder_backup.dump` (or `tminder_backup.dump.gz`) contains **schema, data, indexes, constraints** and can be restored on any PostgreSQL instance of the same major version.

---

## 📤 Share the Dump with a Windows Developer

| Method | How to Use |
|--------|------------|
| **Git LFS / Repository** | Add the dump file to a repo (enable LFS for large files) and push.
| **Cloud Storage** | Upload to Google Drive, Dropbox, OneDrive, etc., and share the link.
| **Direct Copy** | Use `scp`, `rsync`, USB stick, or a shared network folder.

> **Security tip** – If the dump contains sensitive data, encrypt it before sharing:
>
> ```bash
> gpg -c tminder_backup.dump.gz   # creates tminder_backup.dump.gz.gpg
> ```
>
> Share the `.gpg` file and the passphrase via a secure channel.

---

## 🪟 Windows – Restore the Dump

1. **Install PostgreSQL** (if not already installed) using the official installer. Remember the super‑user credentials you set during installation.
2. **Place the dump** file somewhere accessible, e.g., `C:\Users\YourName\Downloads\tminder_backup.dump` (or `.gz`).
3. **Open PowerShell** (or Command Prompt) and run:

```powershell
# If you received a .gz file, unzip it first (Windows 10+ includes tar)
# tar -xzf C:\path\to\tminder_backup.dump.gz -C C:\path\to\

# Create a new empty database (choose any name you like)
createdb -U postgres -h localhost -p 5432 tminder_dev

# Restore the dump into the new database
pg_restore -U postgres -h localhost -p 5432 -d tminder_dev -Fc C:\path\to\tminder_backup.dump
```

4. **Verify the import**:

```powershell
psql -U postgres -d tminder_dev -c "\dt"
```

You should see all tables (`media`, `title_episodes`, …) populated with the data you exported on macOS.

---

## ✅ Quick Reference (One‑liner for Windows)

```powershell
createdb -U postgres -h localhost tminder_dev; pg_restore -U postgres -h localhost -d tminder_dev -Fc C:\tmp\tminder_backup.dump
```

---

## 📚 Further Reading

- PostgreSQL Documentation – [pg_dump](https://www.postgresql.org/docs/current/app-pgdump.html) / [pg_restore](https://www.postgresql.org/docs/current/app-pgrestore.html)
- GPG Encryption – <https://www.gnupg.org/documentation/>
