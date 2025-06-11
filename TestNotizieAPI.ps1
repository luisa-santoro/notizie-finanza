 function Test-Request {
    param(
        [string]$method,
        [string]$url,
        [string]$body = $null,
        [int]$expectedStatus,
        [string]$descrizione
    )

    try {
        $params = @{
            Method  = $method
            Uri     = $url
            Headers = @{ 'Content-Type' = 'application/json' }
            ErrorAction = 'Stop'
        }

        if ($body) {
            $params.Body = $body
        }

        Invoke-RestMethod @params | Out-Null
        Write-Host "$descrizione funzionamento corretto (HTTP $expectedStatus)"
    }
    catch {
        $statusCode = $_.Exception.Response.StatusCode.value__
        if ($statusCode -eq $expectedStatus) {
            Write-Host "$descrizione funzionamento corretto (HTTP $statusCode)"
        }
        else {
            Write-Host "$descrizione ERRORE! Atteso HTTP $expectedStatus ma ricevuto HTTP $statusCode"
        }
    }
}


# URL base API
$baseUrl = "http://localhost:8080/notizie"

# 1. GET tutte le notizie
Test-Request -method "GET" -url "$baseUrl" -expectedStatus 200 -descrizione "Richiesta GET tutte le notizie"

# 2. POST nuova notizia
$nuovaNotizia = @{
    titolo = "Notizia Test"
    descrizione = "Descrizione test"
} | ConvertTo-Json -Depth 3
Test-Request -method "POST" -url "$baseUrl" -body $nuovaNotizia -expectedStatus 200 -descrizione "Richiesta POST nuova notizia"

# 3. GET notizia id 1 (modifica se necessario)
Test-Request -method "GET" -url "$baseUrl/1" -expectedStatus 200 -descrizione "Richiesta GET notizia id 1"

# 4. PUT aggiorna notizia id 1
$aggiornaNotizia = @{
    titolo = "Notizia Aggiornata"
    descrizione = "Descrizione aggiornata"
} | ConvertTo-Json -Depth 3
Test-Request -method "PUT" -url "$baseUrl/1" -body $aggiornaNotizia -expectedStatus 200 -descrizione "Richiesta PUT aggiorna notizia id 1"

# 5. DELETE notizia id 1
Test-Request -method "DELETE" -url "$baseUrl/1" -expectedStatus 204 -descrizione "Richiesta DELETE notizia id 1"

# 6. GET notizia eliminata (404 atteso)
Test-Request -method "GET" -url "$baseUrl/1" -expectedStatus 404 -descrizione "Richiesta GET notizia id 1 dopo DELETE"

# 7. POST notizia non valida (manca titolo)
$notiziaInvalida = @{
    descrizione = "Senza titolo"
} | ConvertTo-Json -Depth 3
Test-Request -method "POST" -url "$baseUrl" -body $notiziaInvalida -expectedStatus 400 -descrizione "Richiesta POST non valida"
