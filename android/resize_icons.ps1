Add-Type -AssemblyName System.Drawing
$src = "D:\android-projeler\lexio\lexio_icon_final_512.png"
if (Test-Path $src) {
    $img = [System.Drawing.Image]::FromFile($src)
    $sizes = @{ "mdpi"=48; "hdpi"=72; "xhdpi"=96; "xxhdpi"=144; "xxxhdpi"=192 }
    foreach ($k in $sizes.Keys) {
        $s = $sizes[$k]
        $bmp = New-Object System.Drawing.Bitmap $s, $s
        $g = [System.Drawing.Graphics]::FromImage($bmp)
        $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
        $g.DrawImage($img, 0, 0, $s, $s)
        $dir = "D:\android-projeler\lexio\android\app\src\main\res\mipmap-$k"
        if (!(Test-Path $dir)) {
            New-Item -ItemType Directory -Force -Path $dir | Out-Null
        }
        $bmp.Save("$dir\ic_launcher.png", [System.Drawing.Imaging.ImageFormat]::Png)
        $bmp.Save("$dir\ic_launcher_round.png", [System.Drawing.Imaging.ImageFormat]::Png)
        $bmp.Dispose()
        $g.Dispose()
    }
    
    # Also save a 108x108 version for adaptive icon foreground
    $bmpAdaptive = New-Object System.Drawing.Bitmap 108, 108
    $gAdaptive = [System.Drawing.Graphics]::FromImage($bmpAdaptive)
    $gAdaptive.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $gAdaptive.DrawImage($img, 10, 10, 88, 88)
    
    $adpDir = "D:\android-projeler\lexio\android\app\src\main\res\drawable"
    $bmpAdaptive.Save("$adpDir\ic_launcher_foreground.png", [System.Drawing.Imaging.ImageFormat]::Png)
    $bmpAdaptive.Dispose()
    $gAdaptive.Dispose()

    $img.Dispose()
    Write-Output "Icons generated successfully."
} else {
    Write-Output "Source file not found!"
}
